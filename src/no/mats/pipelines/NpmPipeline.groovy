package no.mats.pipelines;

def pipeline(options) {

    stage ('Install') {
      echo 'Install'

      sh 'npm i'
    }
}

return this;