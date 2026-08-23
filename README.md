# User Info Form — Production-Style DevOps Mini Project

A small Java/Spring Boot user-information application demonstrating an end-to-end DevOps workflow:

Git → Jenkins CI/CD → Docker → Container Registry → Kubernetes on AWS EC2

## Application

The application provides a web form for:

- First name
- Last name
- Email
- Phone
- City

Submitted data is persisted in PostgreSQL.

Technology stack:

- Java 21
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA
- PostgreSQL
- Maven
- Docker multi-stage build
- Jenkins
- Kubernetes
- NGINX Ingress
- AWS EC2

## Recommended AWS mini-production layout

Use four EC2 instances:

1. `jenkins-01` — Jenkins controller
2. `k8s-control-01` — Kubernetes control plane
3. `k8s-worker-01` — Kubernetes worker
4. `k8s-worker-02` — Kubernetes worker

For a learning environment, K3s is used because it is lightweight. For a real production environment, prefer EKS or an enterprise-supported Kubernetes distribution.

## Repository structure

```text
user-info-devops/
├── src/
├── pom.xml
├── Dockerfile
├── .dockerignore
├── Jenkinsfile
├── k8s/
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── postgres.yaml
│   ├── app-deployment.yaml
│   ├── app-service.yaml
│   └── ingress.yaml
└── scripts/
    ├── install-k3s-server.sh
    ├── install-k3s-agent.sh
    └── deploy.sh
```

## Local run

```bash
mvn clean package
java -jar target/user-info-app-1.0.0.jar
```

Open:

```text
http://localhost:8080
```

## Docker

```bash
docker build -t user-info-app:1.0.0 .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/userdb \
  -e SPRING_DATASOURCE_USERNAME=userapp \
  -e SPRING_DATASOURCE_PASSWORD=change-me \
  user-info-app:1.0.0
```

## Kubernetes

Create the namespace and database secret first:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/app-deployment.yaml
kubectl apply -f k8s/app-service.yaml
kubectl apply -f k8s/ingress.yaml
```

Check:

```bash
kubectl get pods -n user-info
kubectl get svc -n user-info
kubectl get ingress -n user-info
kubectl logs -n user-info deployment/user-info-app
```

## Jenkins pipeline

Create these Jenkins credentials:

- `docker-registry-creds` — registry username/password
- `kubeconfig-user-info` — kubeconfig file credential

Set the Jenkins environment variables in the Jenkinsfile or preferably as a Jenkins global/folder environment:

```text
REGISTRY_HOST=your-registry.example.com
IMAGE_REPO=your-registry.example.com/user-info-app
K8S_NAMESPACE=user-info
```

The pipeline performs:

1. Checkout
2. Unit tests
3. Maven package
4. Docker build
5. Docker push
6. Kubernetes deployment
7. Rollout verification

## Production improvements

For a real production implementation:

- Use AWS EKS instead of self-managed K3s where appropriate.
- Use RDS PostgreSQL instead of PostgreSQL inside Kubernetes.
- Store secrets in AWS Secrets Manager / External Secrets.
- Use ECR instead of a public Docker registry.
- Use an ALB/NLB and TLS certificates.
- Use separate dev/stage/prod namespaces or clusters.
- Add Prometheus/Grafana and centralized logging.
- Add image scanning with Trivy.
- Add SAST/dependency scanning.
- Use Helm or Kustomize.
- Use GitOps with Argo CD for larger environments.
- Add backup/restore and database migration tooling.
