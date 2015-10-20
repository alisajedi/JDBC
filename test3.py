import cx_Oracle #Accessing Oracle in Python
import getpass
import sys

try:
    print("user: sajediba")
    password = getpass.getpass()
    con = cx_Oracle.connect("sajediba", password, "gwynne.cs.ualberta.ca:1521/CRS")
    curs = con.cursor()
    #curs.execute("create table movie(movie_number integer, title char(20), primary key(movie_number))")
    #curs.execute("insert into movie values(1, 'Chicago')")
    #curs.execute("insert into movie values(2, 'Mazerunner')")
    #curs.execute("insert into movie values(3, 'Taken')")
    curs.execute("declare tableExists int; " + 
                 " begin " +
                 " select count(table_name) into tableExists from user_tables where table_name = 'STUDENT'; " +
                 " if tableExists = 1 then " +
                 " execute immediate 'drop table STUDENT'; " + 
                 " end if; " + 
                 " end;")
    curs.execute("create table Student(id int, name char(10), height float, primary key(id))")

    data = [(1, 'John', 166.3), (2, 'Jill', 177.8), (3, 'Jack', 163.5)]
    curs.bindarraysize = 3
    curs.setinputsizes(int, 10, float)
    curs.executemany("insert into student(id, name, height) values (:1, :2, :3)", data)
    con.commit()
    
    curs.execute("select * from student")
    rows = curs.fetchall()
    for row in rows:
        print(row)

    #Getting metadata:
    rows = curs.description
    columnCount = len(rows)
    # display column names and type
    # (name, type, display_size,internal_size,precision,scale,null_ok)
    for row in rows:
	    print(row[0]," ",row[1])
    
    curs.execute("commit")
    curs.close()
    con.close()

except Exception as e:
    print(e)
