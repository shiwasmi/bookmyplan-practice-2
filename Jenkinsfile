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

        //stage('SonarQube Code Quality') {
        //    environment {
        //       scannerHome = tool 'qube'
        //   }
        //   steps {
        //        echo 'Starting SonarQube Code Quality Scan...'
        //        withSonarQubeEnv('sonar-server') {
        //            sh 'mvn sonar:sonar'
        //        }
        //        echo 'SonarQube Scan Completed. Checking Quality Gate...'
        //        timeout(time: 10, unit: 'MINUTES') {
        //            waitForQualityGate abortPipeline: true
        //        }
        //        echo 'Quality Gate Check Completed!'
        //    }
        //}

        stage('Code Package') {
            steps {
                echo 'Creating Artifact...'
                sh 'mvn package'
                sh '''
                    # If WAR is expected
                    cp target/*.war target/bookmyplan-practice-${BUILD_NUMBER}.war
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

        stage('Push Docker Image to Amazon ECR') {
            steps {
                script {
                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'ecr-credentials']]) {
                        sh '''
                            aws ecr get-login-password --region ap-south-1 | \
                              docker login --username AWS --password-stdin 251335054837.dkr.ecr.ap-south-1.amazonaws.com

                            docker tag bookmyplan-practice:latest 251335054837.dkr.ecr.ap-south-1.amazonaws.com/sagardocker:bookmyplan-practice-latest
                            docker push 251335054837.dkr.ecr.ap-south-1.amazonaws.com/sagardocker:bookmyplan-practice-latest
                        '''
                        echo 'Docker Image Pushed to Amazon ECR Successfully!'
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
                docker rmi 251335054837.dkr.ecr.ap-south-1.amazonaws.com/sagardocker:bookmyplan-practice-latest || echo "Image not found or already deleted"
                docker image prune -f
                '''
                echo 'Local Docker Images Cleaned Up Successfully!!'
            }
        }
    }
}