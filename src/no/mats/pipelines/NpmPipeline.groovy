package no.mats.pipelines

import groovy.json.JsonSlurper 

def run(Map<String, String> options) {

  def defaultOptions = [
    deploy: false
  ]

  options = defaultOptions + defaultOptions

  node {
    def image
    def packageJson

    stage('Get docker image') {
      image = docker.image('node:10-slim')
      image.inside {
        echo 'Image is ready'
      }
    }

    stage('Checkout') {
      checkout scm
      println pwd()
      def jsonSlurper = new JsonSlurper()
      packageJson = jsonSlurper.parse(new File('./package.json'))
    }
    
    image.inside {

      stage('Install') {
        sh 'npm i'
      }

      if (packageJson.scripts.lint-ci) {

        stage('Lint') {
          sh 'npm run lint-ci'
        }

      }

      if (packageJson.scripts.build-ci) {

        stage('Build') {
          sh 'npm run build-ci'
        }

      }

      if (packageJson.scripts.test-ci) {

        stage('Test') {
          sh 'npm run test-ci'
        }

      }

      if (options.deploy) {

        stage('Deploy to Beta') {

        }
      }
    }
  }
}