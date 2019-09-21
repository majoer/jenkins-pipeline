package no.mats.pipelines;

def run(Map<String, String> options) {
  pipeline {
    agent any

    stages {
      stage('Install') {
        steps {
          echo 'Install'
          sh 'npm i'
        }
      }
    }
  }
}