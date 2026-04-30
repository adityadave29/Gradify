FROM golang:1.22-alpine AS builder

WORKDIR /build

COPY go.mod ./
# COPY go.sum ./ # Uncomment if go.sum exists
RUN go mod download

COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o api-gateway .

FROM alpine:latest

# Install ca-certificates to allow HTTPS requests (if needed)
RUN apk --no-cache add ca-certificates

WORKDIR /app

COPY --from=builder /build/api-gateway /app/api-gateway

EXPOSE 8080

CMD ["./api-gateway"]
