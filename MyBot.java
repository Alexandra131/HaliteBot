import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class MyBot {

    private final static InitPackage initPackage = Networking.getInit();
    private final static int playerId = initPackage.myID;
    private final static GameMap gameMap = initPackage.map;
    private static boolean[][] moved = new boolean[gameMap.width][gameMap.height];

    private static void resetMoves() {
        for (int i = 0; i < gameMap.width; i++) {
            Arrays.fill(moved[i], false);
        }
    }

    private static boolean isBorder(Location loc) {
        for (Direction dir : Direction.CARDINALS) {
            if (gameMap.getLocation(loc, dir).getSite().owner != playerId) {
                return true;
            }
        }
        return false;
    }

    private static double calculateValue(Location loc) {
        Site site = loc.getSite();
        return (site.production + 1.0) / (site.strength + 1.0);
    }

    private static boolean isFriendly(Location loc) {
        return loc.getSite().owner == playerId;
    }

    private static Move getBestMove(Location loc) {
        Site site = loc.getSite();
        if (site.strength < site.production * 5) {
            return new Move(loc, Direction.STILL);
        }

        Direction bestDirection = Direction.STILL;
        double bestValue = -1;
        for (Direction dir : Direction.CARDINALS) {
            Location neighbor = gameMap.getLocation(loc, dir);
            Site neighborSite = neighbor.getSite();
            if (neighborSite.owner != playerId && neighborSite.strength < site.strength) {
                double value = calculateValue(neighbor);
                if (value > bestValue) {
                    bestValue = value;
                    bestDirection = dir;
                }
            }
        }
        return new Move(loc, bestDirection);
    }

    private static void markMoved(Move move) {
        moved[move.loc.getX()][move.loc.getY()] = true;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Networking.sendInit("HaliteBot");

        while (true) {
            List<Move> moves = new ArrayList<>();
            Networking.updateFrame(gameMap);
            resetMoves();

            for (int y = 0; y < gameMap.height; y++) {
                for (int x = 0; x < gameMap.width; x++) {
                    Location loc = gameMap.getLocation(x, y);
                    if (isFriendly(loc) && !moved[x][y]) {
                        Move move = getBestMove(loc);
                        moves.add(move);
                        markMoved(move);
                    }
                }
            }

            Networking.sendFrame(moves);
        }
    }
}