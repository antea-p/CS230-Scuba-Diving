CREATE SCHEMA IF NOT EXISTS scuba_diving;
USE scuba_diving;

CREATE TABLE User (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(50) NOT NULL,
  email VARCHAR(50) NOT NULL,
  is_admin BOOLEAN NOT NULL
);

CREATE TABLE Location (
  location_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  zip_code VARCHAR(10),
  country VARCHAR(50) NOT NULL
);

CREATE TABLE Booking (
  booking_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  arrival_date DATE NOT NULL,
  return_date DATE NOT NULL,
  payment_date DATE NOT NULL,
  total_price DECIMAL(10, 2) NOT NULL,
  location_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES User(user_id),
  FOREIGN KEY (location_id) REFERENCES Location(location_id)
);

CREATE TABLE Category (
  category_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) UNIQUE NOT NULL,
  description VARCHAR(255) NOT NULL
);

CREATE TABLE Item (
  item_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(100) UNIQUE NOT NULL,
  description VARCHAR(255) NOT NULL,
  cost DECIMAL(10, 2) NOT NULL,
  category_id INT NOT NULL,
  FOREIGN KEY (category_id) REFERENCES Category(category_id)
);

CREATE TABLE BookingItem (
  booking_id INT NOT NULL,
  item_id INT NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (booking_id, item_id),
  FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
  FOREIGN KEY (item_id) REFERENCES Item(item_id)
);

INSERT INTO User (user_id, username, password, email, is_admin) VALUES
(1, 'johnsmith', 'password123', 'jsmith@gmail.com', 0),
(2, 'janedoe', 'securepass', 'jdoe@gmail.com', 0),
(3, 'admin', 'adminpass', 'admin@admin.com', 1),
(4, 'scubadiver', 'diving123', 'scubadiver@gmail.com', 0);

INSERT INTO Location (location_id, name, zip_code, country) VALUES
(1, 'Beach Resort', '12345', 'USA'),
(2, 'Tropical Island', '54321', 'Maldives'),
(3, 'Coral Reef', '67890', 'Australia'),
(4, 'Island Paradise', '11111', 'Bahamas'),
(5, 'Tropical Cove', '22222', 'Fiji'),
(6, 'Seaside Resort', '33333', 'Spain'),
(7, 'Coral Bay', '44444', 'Malaysia'),
(8, 'Oceanic Retreat', '55555', 'Thailand');

INSERT INTO Booking (booking_id, user_id, arrival_date, return_date, payment_date, total_price, location_id) VALUES
(1, 1, '2023-06-15', '2023-06-20', '2023-06-20', 500.00, 1),
(2, 2, '2023-07-10', '2023-07-15', '2023-07-15', 700.00, 2),
(3, 4, '2023-08-05', '2023-08-12', '2023-08-12', 1200.00, 3);

INSERT INTO Category (category_id, title, description) VALUES
(1, 'Courses', 'Courses led by certified diving instructors'),
(2, 'Diving Equipment', 'Scuba diving gear and equipment'),
(3, 'Boats', 'Boats for scuba diving trips'),
(4, 'Other', 'Other scuba diving related items, such as deep sea cameras or lighting');

INSERT INTO Item (item_id, title, description, cost, category_id) VALUES
(1, 'Open Water Certification Course', 'Beginner scuba diving course', 250.00, 1),
(2, 'Dive Computer', 'Advanced dive computer with GPS', 350.00, 2),
(3, 'Snorkel Set', 'High-quality snorkel set with fins', 50.00, 2),
(4, 'Dive Rib', 'Rigid inflatable boat (RIB) equipped for scuba diving adventures', 800.00, 3),
(5, 'Underwater Camera', 'Professional underwater camera with 4K resolution', 500.00, 4),
(6, 'Dive Light', 'Powerful dive light for night dives', 150.00, 4),
(7, 'Wetsuit', '3mm neoprene wetsuit for warm water diving', 150.00, 2),
(8, 'Regulator Set', 'High-performance regulator set with octopus', 500.00, 2),
(9, 'Underwater Scooter', 'Electric underwater scooter for effortless diving', 800.00, 4),
(10, 'Dive Knife', 'Durable stainless steel dive knife with sheath', 75.00, 4),
(11, 'Scuba Tank', 'Aluminum scuba tank with DIN valve', 250.00, 2),
(12, 'Dry Bag', 'Waterproof dry bag for keeping belongings dry during dives', 40.00, 4),
(13, 'Dive Watch', 'Dive watch with depth gauge and compass', 300.00, 4),
(14, 'Freediving Fins', 'Long-blade carbon fiber freediving fins', 200.00, 2),
(15, 'Rebreather', 'Advanced closed-circuit rebreather for extended dives', 3000.00, 4),
(16, 'Inflatable Kayak', 'Two-person inflatable kayak for scuba diving exploration', 300.00, 3),
(17, 'Speedboat', 'Powerful speedboat for quick access to dive sites', 1500.00, 3),
(18, 'Catamaran', 'Spacious catamaran with diving platform for group dives', 2500.00, 3),
(19, 'Advanced Diver Course', 'Specialized course for advanced scuba diving skills', 400.00, 1),
(20, 'Rescue Diver Course', 'Comprehensive training in rescue and emergency response', 450.00, 1),
(21, 'Divemaster Course', 'Professional-level training for becoming a dive leader', 800.00, 1),
(22, 'Underwater Photography Course', 'Learn techniques for capturing stunning underwater photos', 350.00, 1),
(23, 'Technical Diving Course', 'Advanced training for deep diving and decompression dives', 600.00, 1);


INSERT INTO BookingItem (booking_id, item_id, quantity) VALUES
(1, 1, 3),
(1, 2, 5),
(2, 3, 1),
(2, 4, 2),
(3, 5, 4),
(3, 6, 10);
