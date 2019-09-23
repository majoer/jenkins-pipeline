package no.mats.pipelines;

def run(Map<String, String> options) {
  node {
    
    docker.image('node:10').withRun() { c ->
      stage('Install') {
        echo 'Install'
        sh 'npm i'
      }
    }
  }
}