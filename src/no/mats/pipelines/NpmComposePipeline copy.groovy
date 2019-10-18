package no.mats.pipelines

import groovy.json.JsonSlurper 

def compose(Closure inner) {
  try {
      sh "docker-compose up --build -d"
      inner.call()
  } finally {
      sh "docker-compose down"
  }
}

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
      nodeImage = docker.image("node:10-alpine")
      nodeImage.inside {
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
    
    compose { () ->
      nodeImage.inside {

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