pipeline {
    agent {
        label "slave_node_java"
    }

    environment {
        DOCKER_IMAGE = "webapp"
        CONTAINER_NAME = "app"
        CONTAINER_PORT = "80"
        REQUEST_PORT = "8080"
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

        stage("Build Docker Image") {
            steps {
                sh "sudo docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage("Remove Old Container") {
            steps {
                sh "sudo docker rm -f ${CONTAINER_NAME} || true"
            }
        }

        stage("Run Docker Container") {
            steps {
                sh """
                sudo docker run -d \
                --name ${CONTAINER_NAME} \
                -p ${CONTAINER_PORT}:${REQUEST_PORT} \
                ${DOCKER_IMAGE}
                """
            }
        }

        stage("Cleanup Old Images") {
            steps {
                sh """
                sudo docker rmi -f ${DOCKER_IMAGE} || true
                """
            }
        }
        stage('Deploy using Ansible') {
            steps {
                sh '''
                /usr/bin/ansible-playbook -vvv -i ansible/inventory ansible/playbook.yml
                '''
            }
        }
        
    }
    post {
        always {
            cleanWs()
        }
    }
}