package no.mats.pipelines;

def run(Map<String, String> options) {
  pipeline {

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