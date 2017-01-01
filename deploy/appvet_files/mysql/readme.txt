To run these scripts, go into mysql:

mysql --user=username --password=password

mysql> create database appvet;
mysql> exit;

Next, load the appvet tables:

mysql --user=username --password=password appvet < appvet_x.y.z_init_db.sql

