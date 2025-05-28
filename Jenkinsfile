pipeline {
	agent { label 'Laptop-Node' }

    triggers {
        pollSCM '* * * * *'
    }

    options {
        disableConcurrentBuilds abortPrevious: true
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
            }
        }

        stage('Jacoco code coverage'){
//             when {
//                 branch 'main'
//             }
            steps {
                sh './gradlew testReleaseUnitTestCoverage'
            }
            post {
                always {
                    junit 'app/build/test-results/**/*.xml'
                    publishHTML target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: true,
                            reportDir: 'app/build/reports/jacoco/testReleaseUnitTestCoverage/html',
                            reportFiles: 'index.html',
                            reportName: 'Jacoco Report'
                        ]
                    }
            }
        }

        stage('Coverage Check (Fail if < 70%)') {
            steps {
                sh './gradlew testReleaseUnitTestCoverageVerification'
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
            steps {
                sh('rm -rf .signing')
            }
        }
    }
}