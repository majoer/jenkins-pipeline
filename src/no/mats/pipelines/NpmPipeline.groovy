package no.mats.pipelines;

class NpmPipeline {

  NpmPipeline(Map<String, String> options) {
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