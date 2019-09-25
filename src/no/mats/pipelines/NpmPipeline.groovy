package no.mats.pipelines;

def run(Map<String, String> options) {
  node {
    def image;

    stage('Get docker image') {
      image = docker.image('node:10-slim')
    }
    
    image.inside {

      stage('Install') {
        sh 'npm i'
      }

    }
  }
}