## Pre-requisite
Pricing DB should be running from the pricing-service repo

## Setup localstack
```shell
docker run -d --name localstack -p 4566:4566 localstack/localstack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
aws --endpoint-url=http://localhost:4566 s3 mb s3://price-bucket
```