FROM golang:1.22-alpine AS builder

WORKDIR /build

COPY go.mod ./
# COPY go.sum ./ # Uncomment if go.sum exists
RUN go mod download

COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -o stats-service .

FROM alpine:latest

# Install ca-certificates
RUN apk --no-cache add ca-certificates

WORKDIR /app

COPY --from=builder /build/stats-service /app/stats-service

ENV SUPABASE_SERVICE_ROLE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhleXV2bHdlcGVwdm1lbnp3YXJxIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MzkwNTEyOSwiZXhwIjoyMDg5NDgxMTI5fQ.RXax6RMpJPqRAN7cZsEoM-aTVhYkehDHDmfMPU-yMgs"
ENV SUPABASE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhleXV2bHdlcGVwdm1lbnp3YXJxIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MzkwNTEyOSwiZXhwIjoyMDg5NDgxMTI5fQ.RXax6RMpJPqRAN7cZsEoM-aTVhYkehDHDmfMPU-yMgs"
EXPOSE 8086

CMD ["./stats-service"]
