db = db.getSiblingDB('wallet-db');

db.createUser({
  user: "root",
  pwd: "root",
  roles: [
    { role: "readWrite", db: "wallet-db" },
    { role: "userAdmin", db: "wallet-db" }
  ]
});
