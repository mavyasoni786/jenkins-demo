pipeline {
	agent { label 'laptop' }

    triggers {
        pollSCM '* * * * *'
    }

    stages {
        stage('Test') {
            steps {
				sh './gradlew clean test'
            }
        }

        stage('Build Apk') {
             steps {
                sh './gradlew clean assemble'
             }
        }
    }
}