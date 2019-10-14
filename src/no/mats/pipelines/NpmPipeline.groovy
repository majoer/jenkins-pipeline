package no.mats.pipelines

import groovy.json.JsonSlurper 

def run(Map<String, String> options) {

  def defaultOptions = [
    deploy: false
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
      
      shouldLint = scripts["lint-ci"]
      shouldBuild = scripts["build-ci"]
      shouldTest = scripts["test-ci"]
    }
    
    image.inside {

      stage("Install") {
        sh "npm i"
      }

      if (shouldLint) {

        stage("Lint") {
          sh "npm run lint-ci"
        }

      }

      if (shouldBuild) {

        stage("Build") {
          sh "npm run build-ci"
        }

      }

      if (shouldTest) {

        stage("Test") {
          sh "npm run test-ci"
        }

      }

      if (options.deploy) {

        stage("Deploy to Beta") {

        }
      }
    }
  }
}