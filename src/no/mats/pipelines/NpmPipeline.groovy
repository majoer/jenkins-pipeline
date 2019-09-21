package no.mats.pipelines;

class NpmPipeline {

  NpmPipeline(options) {
    pipeline {
      agent any
      environment {
        NODE_ENV = 'production'
      }

      stages {

        stage('Install') {
          steps {
            echo 'Install'

            sh 'npm i'
          }
        }

        stage('Build') {

        }
      }
    }
  }
}