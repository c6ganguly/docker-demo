pipeline {
  agent any

  environment {
    DOCKERHUB_CREDENTIALS_ID = 'dockerhub'
    DOCKERHUB_REPO = '070889/springboot-rest' // CHANGE THIS
    DEPLOYMENT_NAME = 'springboot-deployment'
    K8S_MANIFEST = 'k8s/deployment.yaml'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script {
          SHORT_SHA = sh(script: "git rev-parse --short=7 HEAD", returnStdout: true).trim()
          BR = env.BRANCH_NAME ?: 'devopes-demo'
          IMAGE_TAG = "${DOCKERHUB_REPO}:${BR}-${SHORT_SHA}"
          echo "IMAGE_TAG => ${IMAGE_TAG}"
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        sh "docker build -t ${IMAGE_TAG} ."
      }
    }

    stage('Push Image to Docker Hub') {
      steps {
        withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS_ID}",
                                         usernameVariable: 'DOCKER_USER',
                                         passwordVariable: 'DOCKER_PASS')]) {
          sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
          sh "docker push ${IMAGE_TAG}"
          sh "docker logout"
        }
      }
    }

    stage('Deploy to Minikube') {
      steps {
        withCredentials([file(credentialsId: 'kubeconfig-linux', variable: 'KUBECONFIG_FILE')]) {
          sh '''
            mkdir -p $HOME/.kube
            cp $KUBECONFIG_FILE $HOME/.kube/config
            sed -e "s|IMAGE_PLACEHOLDER|${IMAGE_TAG}|g" ${K8S_MANIFEST} > k8s/deployment-for-apply.yaml
            kubectl apply -f k8s/deployment.yaml
            kubectl apply -f k8s/service.yaml || true
            kubectl rollout status deployment/${DEPLOYMENT_NAME} --timeout=120s
          '''
        }
      }
    }
  }

  post {
    success { echo "✅ Deployed ${IMAGE_TAG}" }
    failure { echo "❌ Build or deploy failed" }
  }
}