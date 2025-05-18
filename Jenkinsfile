pipeline {
	agent { label 'laptop' }

    triggers {
        pollSCM '* * * * *'
    }

    environment {
        APP_KEY = credentials("secret-app-key")
    }

    stages {
        stage('Test') {
            steps {
				sh './gradlew clean test'
				echo "APP KEY : ${APP_KEY}"
            }
        }

        stage('Build Apk') {
             steps {
                sh './gradlew clean assemble'
             }
        }
    }
}