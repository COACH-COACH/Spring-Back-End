node {
    withCredentials([[$class: 'UsernamePasswordMultiBinding',
        credentialsId: 'coachcoach-docker-username',
        usernameVariable: 'DOCKER_USER_ID',
        passwordVariable: 'DOCKER_USER_PASSWORD'
    ]]) {
        stage('Pull') {
            git branch: 'main', credentialsId: 'github-username-token-credential', url: 'https://github.com/COACH-COACH/Spring-Back-End.git'
        }

        stage('Build and Push') {
            sh 'sudo docker-compose build'
            sh 'sudo docker login -u $DOCKER_USER_ID -p $DOCKER_USER_PASSWORD'
            sh 'sudo docker-compose push'
        }

        stage('Deploy') {
            sshagent(credentials: ['ec2-springboot-server-ssh']) {
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@3.39.168.72 "sudo docker-compose -f /path/to/docker-compose.yml pull"'
                sh 'ssh -o StrictHostKeyChecking=no ubuntu@3.39.168.72 "sudo docker-compose -f /path/to/docker-compose.yml up -d"'
            }
        }
    }
}
