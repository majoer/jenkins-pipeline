package no.mats.pipelines

import no.mats.deployments.*

def withDockerNetwork(Closure inner) {
  try {
      networkId = UUID.randomUUID().toString()
      sh "docker network create ${networkId}"
      inner.call(networkId)
  } finally {
      sh "docker network rm ${networkId}"
  }
}

def ci(command) {
  sh "env NODE_ENV=ci ${command}"
}

def debugCwd() {
  sh '''
    set +x
    echo ---- Debug ----
    pwd
    echo ''
    ls -a
    echo ---- Debug ----
    set -x
  '''
}

def start(Map<String, Object> options = [:]) {

  def defaultOptions = [
    deploy: false,
    deployFromBranch: 'master',
    deployMethod: 'git',
    s3Bucket: null,
    s3CloudFrontDistId: null,
    s3DeployFolder: './dist',
    s3Region: null,
    scriptLint: 'lint:ci',
    scriptBuild: 'build:ci',
    scriptTest: 'test:ci',
    withPostgres: false,
    credentialsIdDeployHook: ''
  ]

  options = defaultOptions + options

  node {
    sh "echo"
    def nodeImage = docker.image("node:12-slim")
    def shouldLint = false
    def shouldBuild = false
    def shouldTest = false
    deleteDir()

    stage("Checkout") {
      checkout scm
      
      def packageJson = readJSON(file: "${pwd()}/package.json")
      def scripts = packageJson.scripts;
      
      shouldLint = scripts[options.scriptLint]
      shouldBuild = scripts[options.scriptBuild]
      shouldTest = scripts[options.scriptTest]
    }
    
    withDockerNetwork { n ->
      nodeImage.inside("--network ${n}") {

        stage("Install") {
          ci("npm ci")
        }

        if (shouldLint) {

          stage("Lint") {
            ci("npm run ${options.scriptLint}")
          }

        }

        if (shouldBuild) {

          stage("Build") {
            ci("npm run ${options.scriptBuild}")
          }

        }
      }

      if (shouldTest) {
        
        stage("Test") {

          if (options.withPostgres) {

            def postgresImage = docker.image('postgres:12-alpine')
            
            postgresImage.withRun("-e POSTGRES_USER=test -e POSTGRES_DB=bookmarkdb --network ${n} --name=postgres") { c ->
              postgresImage.inside("--network ${n}") {
                sh 'while ! pg_isready -U test -d bookmarkdb -h postgres; do sleep 1; done'
              }

              nodeImage.inside("--network ${n}") {
                ci("npm run ${options.scriptTest}")
              }
            }
          } else {
            ci("npm run ${options.scriptTest}")
          }
        }
      }
    }

    if (options.deploy && options.deployFromBranch == BRANCH_NAME) {

      stage("Deploy to Beta") {
        switch (options.deployMethod) {
          case 'serverless':
            new ServerlessDeployment().deploy(options, nodeImage)
            break
          case 's3':
            new S3Deployment().deploy(options, nodeImage)
            break
          case 'git':
            new GitDeployment().deploy(options)
            break
          default: error("Unknown deployMethod: " + options.deployMethod)
        }
      }
    }
  }
}