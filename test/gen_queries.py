
import random
a=50
b=1
#print a*(	b+1)+1
st=random.randrange(1,100)
for i in range(1,a):
	print "Update user_details set status = %d where user_id = %d"%(st,i)	
	#print 'UPDATE user_details SET username="abcdefg%d" where user_id=%d;'%((i),100-i)
	for k in range(b):
			#print "SELECT * from user_details "
			#print 'SELECT username,password from user_details where user_id > 90;'
			print 'SELECT * from user_details where user_id = %d;'%(10*i+k)
