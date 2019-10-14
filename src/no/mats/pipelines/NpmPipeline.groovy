package no.mats.pipelines

import groovy.json.JsonSlurper 

def run(Map<String, String> options) {

  def defaultOptions = [
    deploy: false,
    scriptLint: 'lint:ci',
    scriptBuild: 'build:ci',
    scriptTest: 'test:ci'
  ]

  options = defaultOptions + defaultOptions

  node {
    def image
    def shouldLint = false
    def shouldBuild = false
    def shouldTest = false

    stage("Get docker image") {
      image = docker.image("node:10-slim")
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