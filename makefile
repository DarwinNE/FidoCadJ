.PHONY: compile createjar clean install createdoc

NAME = fidocadj
VERSION = 0.24.6

default: | compile createjar clean

compile:
	./dev_tools/compile

createjar:
	./dev_tools/createjar

createdoc:
	./dev_tools/createdoc

clean:
	./dev_tools/clean

install:
	./OSes/linux/install
