create table user_payment_information(
id int not null primary key, 
UserID int not null, 
PaymentMethod varchar(50) not null, 
foreign key (UserID) references users(id)
on delete cascade); 

create table booking_payments(
PaymentInformationID int not null, 
ReservationID int not null, 
primary key (PaymentInformationID, ReservationID),
foreign key (PaymentInformationID) references user_payment_information(id),
foreign key (ReservationID) references reservations(id)
on delete cascade); 

insert into user_payment_information(id,userid,paymentmethod) values
(1,1,"Credit Card"),
(2,1,"PayPal"),
(3,2,"Credit Card"),
(4,3,"PayPal"),
(5,3,"Credit Card"),
(6,4,"Credit Card"); 

insert into booking_payments(paymentinformationid,reservationid) values
(4,1),
(5,2),
(4,3);