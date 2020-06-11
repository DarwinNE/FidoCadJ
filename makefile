
NAME = fidocadj
VERSION = 0.24.8

.PHONY: compile   \		# Compile FidoCadJ
		createjar \		# Prepare fidocadj.jar
		createdoc \		# Run Javadoc on all source files
		clean     \		# Erase all the compiled classes
		cleanall  \		# Do a clean, erase fidocadj.jar, Javadocs
		run       \		# Run FidoCadJ
		rebuild   \		# Do a clean and then run FidoCadJ
		install			# Install FidoCadJ

default: compile

compile:
	@./dev_tools/compile  $(ARGS)

createjar:
	@./dev_tools/createjar

createdoc:
	@./dev_tools/createdoc

clean:
	@./dev_tools/clean

cleanall:
	@./dev_tools/cleanall

rebuild: | clean   	\
		   compile 	\
		   run
run:
	@./dev_tools/run $(ARGS)

install: createjar
	@./OSes/linux/install
