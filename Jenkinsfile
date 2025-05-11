pipeline {
	agent { label 'android-jdk17' }

    options {
		disableConcurrentBuilds abortPrevious: true
    }
    stages {
        stage('Test') {
            steps {
				sh './gradlew clean test1'
            }
            }

        stage('Build Apk') {
             steps {
                sh './gradlew clean assemble'
             }
        }
    }
}