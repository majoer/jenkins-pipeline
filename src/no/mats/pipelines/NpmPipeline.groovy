package no.mats.pipelines;

def pipeline(Map<String, String> options) {

    stage ('Install') {
      echo 'Install'

      sh 'npm i'
    }
}