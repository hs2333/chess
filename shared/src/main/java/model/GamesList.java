package model;
import chess.ChessGame;

import java.util.HashSet;

public record GamesList(HashSet<GameData> games) {}