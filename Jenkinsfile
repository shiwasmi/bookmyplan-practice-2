pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }

    tools {
        maven 'mvn_3.9.16'
    }

    stages {
        stage('Code Compilation') {
            steps {
                echo 'Starting Code Compilation...'
                sh 'mvn clean compile'
                echo 'Code Compilation Completed Successfully!'
            }
        }

        stage('Code QA Execution') {
            steps {
                echo 'Running JUnit Test Cases...'
                sh 'mvn test'
                echo 'JUnit Test Cases Completed Successfully!'
            }
        }

        stage('Code Package') {
            steps {
                echo 'Creating Artifact...'
                sh 'mvn package'
                sh '''
                    # If WAR is expected
                    cp target/*.war target/bookmyplan-practice-2-${BUILD_NUMBER}.war
                '''
                archiveArtifacts artifacts: 'target/bookmyplan-practice-2-*.war', fingerprint: true
                echo 'Artifact Created Successfully!!'
            }
        }

        stage('Build & Tag Docker Image') {
            steps {
                sh "docker build -t sagardocker/bookmyplan-practice:latest -t bookmyplan-practice:latest ."
            }
        }

        stage('Docker Image Scanning') {
            steps {
                echo 'Scanning Docker Image with Trivy...'
                sh 'trivy image sagardocker/bookmyplan-practice:latest || echo "Scan Failed - Proceeding with Caution"'
                echo 'Docker Image Scanning Completed!'
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhubCred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
                        sh "docker tag bookmyplan-practice:latest $DOCKER_USER/bookmyplan-practice:latest"
                        sh "docker push $DOCKER_USER/bookmyplan-practice:latest"
                    }
                }
            }
        }

        stage('Clean Up Local Docker Images') {
            steps {
                echo 'Cleaning Up Local Docker Images...'
                sh '''
                docker rmi sagardocker/bookmyplan-practice:latest || echo "Image not found or already deleted"
                docker rmi bookmyplan-practice:latest || echo "Image not found or already deleted"
                docker image prune -f
                '''
                echo 'Local Docker Images Cleaned Up Successfully!!'
            }
        }
    }
}