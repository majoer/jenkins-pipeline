package no.mats.pipelines

import groovy.json.JsonSlurper 

def run(Map<String, String> options) {

  def defaultOptions = [
    deploy: false,
    scriptLint: 'lint:ci',
    scriptBuild: 'build:ci',
    scriptTest: 'test:ci',
    withPostgres: false
  ]

  options = defaultOptions + options

  node {
    def image
    def shouldLint = false
    def shouldBuild = false
    def shouldTest = false

    stage("Create Nodejs container") {
      image = docker.image("node:10-alpine")
      image.inside {
        echo "Image is ready"
      }
    }

    stage("Checkout") {
      checkout scm
      def jsonSlurper = new JsonSlurper()
      def packageJson = jsonSlurper.parse(new File("${pwd()}/package.json"))
      def scripts = packageJson.scripts;
      
      shouldLint = scripts[options.scriptLint]
      shouldBuild = scripts[options.scriptBuild]
      shouldTest = scripts[options.scriptTest]
    }
    
    image.inside {

      stage("Install") {
        sh "npm i"
      }

      if (shouldLint) {

        stage("Lint") {
          sh "npm run ${options.scriptLint}"
        }

      }

      if (shouldBuild) {

        stage("Build") {
          sh "npm run ${options.scriptBuild}"
        }

      }
    }

    if (options.withPostgres) {
      stage("Create Postgres container") {

        def postgresImage = docker.image('postgres:12-alpine')
        
        postgresImage.withRun("-e POSTGRES_USER=test -e POSTGRES_DB=bookmarkdb -p 5432:5431") { c ->
          postgresImage.inside {
            // sh 'while ! pg_isready -U test -d bookmarkdb -p 5431; do sleep 1; done'
          }
        }
      }
    }

    image.inside {

      if (shouldTest) {

        stage("Test") {
          sh "npm run ${options.scriptTest}"
        }

      }

      if (options.deploy) {

        stage("Deploy to Beta") {

        }
      }
    }
  }
}