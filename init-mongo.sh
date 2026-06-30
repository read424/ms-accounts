#!/bin/bash

# Wait for MongoDB to start
sleep 10

# Initialize the database with admin user
mongosh --host localhost:27017 --username admin --password bootcamp_accounts_prod_2024 --authenticationDatabase admin << EOF
use ms_accounts_prod;
db.createCollection("accounts");
EOF

echo "MongoDB initialization complete"
