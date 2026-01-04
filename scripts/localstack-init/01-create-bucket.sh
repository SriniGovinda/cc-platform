#!/usr/bin/env bash
set -e
awslocal s3 mb s3://cc-statements || true
echo "LocalStack S3 bucket ready: cc-statements"
