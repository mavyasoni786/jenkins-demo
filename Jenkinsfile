pipeline {
	agent { label 'android-jdk17' }

    options {
		disableConcurrentBuilds abortPrevious: true
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