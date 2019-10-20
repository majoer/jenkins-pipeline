package no.mats.pipelines

import groovy.json.JsonSlurper 

def withDockerNetwork(Closure inner) {
  try {
      networkId = UUID.randomUUID().toString()
      sh "docker network create ${networkId}"
      inner.call(networkId)
  } finally {
      sh "docker network rm ${networkId}"
  }
}

def start(Map<String, Object> options = [:]) {

  def defaultOptions = [
    deploy: false,
    deployFromBranch: 'master',
    scriptLint: 'lint:ci',
    scriptBuild: 'build:ci',
    scriptTest: 'test:ci',
    withPostgres: false
  ]

  options = defaultOptions + options

  node {
    def image
    def shouldLint = false
    def shouldBuild = false
    def shouldTest = false

    stage("Create Nodejs container") {
      nodeImage = docker.image("majoer/node-python:10")
      nodeImage.inside {
        echo "Image is ready"
      }
    }

    stage("Checkout") {
      checkout scm
      def jsonSlurper = new JsonSlurper()
      def packageJson = jsonSlurper.parse(new File("${pwd()}/package.json"))
      def scripts = packageJson.scripts;
      
      shouldLint = scripts[options.scriptLint]
      shouldBuild = scripts[options.scriptBuild]
      shouldTest = scripts[options.scriptTest]
    }
    
    withDockerNetwork { n ->
      nodeImage.inside("--network ${n}") {

        stage("Install") {
          sh "npm i"
        }

        if (shouldLint) {

          stage("Lint") {
            sh "npm run ${options.scriptLint}"
          }

        }

        if (shouldBuild) {

          stage("Build") {
            sh "npm run ${options.scriptBuild}"
          }

        }
      }

      if (shouldTest) {

        if (options.withPostgres) {

          stage("Create Postgres container") {

            def postgresImage = docker.image('postgres:12-alpine')
            
            postgresImage.withRun("-e POSTGRES_USER=test -e POSTGRES_DB=bookmarkdb --network ${n} --name=postgres") { c ->
              postgresImage.inside("--network ${n}") {
                sh 'while ! pg_isready -U test -d bookmarkdb -h postgres; do sleep 1; done'
              }

              nodeImage.inside("--network ${n}") {
                stage("Test") {
                  sh "npm run ${options.scriptTest}"
                }
              }
            }
          }
        } else {
          stage("Test") {
            sh "npm run ${options.scriptTest}"
          }
        }
      }
    }

    if (options.deploy && options.deployFromBranch == BRANCH_NAME) {

      stage("Deploy to Beta") {
        withCredentials([
          usernamePassword(
            credentialsId: 'git-provider-1',
            usernameVariable: 'GIT_USERNAME',
            passwordVariable: 'GIT_PASSWORD')
          ]) {

            sh 'git config --local credential.helper "!p() { echo username=\\$GIT_USERNAME; echo password=\\$GIT_PASSWORD; }; p"'
            sh "git checkout -B release/beta"
            sh "git push --set-upstream origin release/beta"
        }
      }
    }
  }
}