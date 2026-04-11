pipeline {
    agent {
        label "slave_node_java"
    }

    environment {
        DOCKERHUB_USERNAME = credentials('suprit43')
        DOCKERHUB_PASSWORD = credentials('Suprit@145')
        IMAGE_NAME = 'hospital-app'
        VERSION = "${BUILD_NUMBER}"
    }

    stages {
        stage("Install Docker If Not Present") {
            steps {
                sh """
                if ! command -v docker > /dev/null 2>&1; then
                    echo "Docker not found. Installing Docker..."
                    
                    sudo apt-get update
                    sudo apt-get install -y docker.io
                    
                    sudo systemctl start docker
                    sudo systemctl enable docker
                    
                    sudo usermod -aG docker jenkins
                else
                    echo "Docker already installed"
                fi
                """
            }
        }
        stage("Check Docker Version") {
            steps {
                sh "sudo docker --version"
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                docker build -t $DOCKERHUB_USERNAME/$IMAGE_NAME:$VERSION .
                '''
            }
        }

        stage('Login to DockerHub') {
            steps {
                sh '''
                echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin
                '''
            }
        }

        stage('Push Docker Image') {
            steps {
                sh '''
                docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:$VERSION
                docker tag $DOCKERHUB_USERNAME/$IMAGE_NAME:$VERSION $DOCKERHUB_USERNAME/$IMAGE_NAME:latest
                docker push $DOCKERHUB_USERNAME/$IMAGE_NAME:latest
                '''
            }
        }

        stage('Deploy using Ansible') {
            steps {
                sh '''
                ansible-playbook -i ansible/inventory ansible/playbook.yml
                '''
            }
        }
    }

    post {
    always {
        script {
            node {
                sh 'docker system prune -f'
            }
        }
    }
}
}