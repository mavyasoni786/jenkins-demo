pipeline {
	agent { label 'laptop' }

    triggers {
        pollSCM '* * * * *'
    }

    environment {
        APP_KEY = credentials("secret-app-key")
        APP_KEYSTORE = credentials("secret-app-keystore")
        APP_KEYSTORE_CREDENTIALS = credentials("secret-app-keystore-credentials")
    }

    stages {

        stage('configure') {
            steps {
                sh('mkdir .signing')
                sh('echo ${APP_KEYSTORE} | base64 -d > .signing/debug.keystore')
            }
        }

        stage('Test') {
            steps {
				sh './gradlew clean test'
				echo "APP KEY : ${APP_KEY}"
            }
        }

        stage('Build Apk') {
             steps {
                sh './gradlew assemble'
             }
        }

        stage('Publish Apk') {
        	steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/**/*.apk'
            }
        }

        stage('clean up'){
            steps{
                sh('rm -rf .signing')
            }
        }
    }
}