build:
	javac MyBot.java

run:
	java MyBot

verf: build
	./environment/halite -d "50 50" -n 1 -s 42 "make -s run"

check-1: build
	python3 ./run.py --cmd "java MyBot" --round 1

check-2: build
	python3 ./run.py --cmd "java MyBot" --round 2

check-3: build
	python3 ./run.py --cmd "java MyBot" --round 3

clean:
	rm -f *.class *.log ./replays/*.hlt
