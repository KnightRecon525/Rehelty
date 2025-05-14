-- Modifier la colonne URL pour accepter des URLs plus longues
ALTER TABLE destinations MODIFY COLUMN url VARCHAR(1000); 