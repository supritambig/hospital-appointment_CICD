pipeline {
    agent { label "slave_node_java" }

    environment {
        DOCKERHUB_USERNAME = 'suprit43'
        DOCKERHUB_REPO = 'hp_webapp'
        VERSION = "${BUILD_ID}"
    }

    stages {

        stage('Install Maven') {
            steps {
                sh '''
                if ! command -v mvn > /dev/null; then
                    echo "Installing Maven..."
                    sudo apt update
                    sudo apt install maven -y
                else
                    echo "Maven already installed"
                fi
                '''
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn -version'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Install Docker') {
            steps {
                sh '''
                if ! command -v docker > /dev/null; then
                    echo "Installing Docker..."
                    sudo apt update
                    sudo apt install docker.io -y
                    sudo systemctl start docker
                    sudo systemctl enable docker
                    sudo chmod 666 /var/run/docker.sock
                else
                    echo "Docker already installed"
                fi

                docker --version
                '''
            }
        }

        stage("Build Docker Image") {
            steps {
                sh """
                docker build -t ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION} .
                
                # 🔥 IMPORTANT FIX (tag latest)
                docker tag ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION} ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage("Docker Login") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker_login',
                usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    sh "echo $PASS | docker login -u $USER --password-stdin"
                }
            }
        }

        stage("Push Image") {
            steps {
                sh """
                docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${VERSION}
                docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                """
            }
        }

        stage('Deploy via Ansible') {
    steps {
        sshagent(['ec2-key']) {
            sh '''
            ANSIBLE_HOST_KEY_CHECKING=False \
            /usr/bin/ansible-playbook -i inventory playbook.yml --extra-vars "tag=${BUILD_ID}"
            '''
        }
    }
}
        stage("Cleanup") {
            steps {
                sh "docker image prune -f"
            }
        }
    }
}