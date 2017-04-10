
while [ $? != 0 ]
do
	sleep 2
#	rm ckpt*
#	rm -rf ckpt*	
	bash getlast_checkpoint.sh
	./dmtcp_restart_script.sh 
	
	
	
	
done
