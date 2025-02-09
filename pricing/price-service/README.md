Setup DB
```shell
docker run --name pricing-db -e POSTGRES_USER=hello -e POSTGRES_PASSWORD=world -e POSTGRES_DB=pricing -p 5432:5432 -d postgres:latest
```

Setup localstack
```shell
docker run --rm -d --name localstack -p 4566:4566 localstack/localstack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1
aws --endpoint-url=http://localhost:4566 s3 mb s3://price-bucket
```

Test
```shell
curl --location 'http://localhost:8080/customer/1/order/prices' \
--header 'Content-Type: application/json' \
--data '{
  "productIds": ["1", "2", "3", "4"]
}
'
```