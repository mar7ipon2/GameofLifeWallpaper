#ifndef GAME_OF_LIFE_H
#define GAME_OF_LIFE_H

#include <vector>
#include <cstdint>
#include <algorithm>
#include <stdexcept>
#include <cstdlib>

// Structure for memory patch optimization (Chunking + Bounding Box)
struct Chunk {
    bool is_active;
    int chunk_start_x, chunk_end_x, chunk_start_y, chunk_end_y;
    int min_x, max_x, min_y, max_y;

    bool is_active_next;
    int next_min_x, next_max_x, next_min_y, next_max_y;

    // 3x3 matrix of precalculated neighbors to avoid divisions
    int neighbors[9];

    Chunk(int start_x, int end_x, int start_y, int end_y);
};

class GameOfLife {
private:
    int width;
    int height;

    // 1D contiguous matrix of states
    std::vector<uint8_t> grid_current;
    std::vector<uint8_t> grid_next;

    // contiguous vector of chunks
    const int chunk_size;
    int num_chunks_x;
    int num_chunks_y;
    std::vector<Chunk> chunks_current;

    // Internal mathematical helpers
    inline int getIndex(int x, int y) const {
        int xx = ((x % width) + width) % width;
        int yy = ((y % height) + height) % height;
        return yy * width + xx;
    }

    int countAliveBorderNeighbors(int x, int y);
    int countAliveInteriorNeighbors(int x, int y);
    uint8_t rulesConway(int x, int y, int neigh);

public:
    GameOfLife(int w, int h, int c, int patternIndex);

    // Advances one generation in the simulation
    void update();

    // Allows injecting alive cells manually
    void setCell(int x, int y);

    // NEW KEY METHOD FOR JNI:
    // Returns a constant reference to the vector to allow quick copying to Kotlin
    const std::vector<uint8_t>& getGridData() const {
        return grid_current;
    }

    int getWidth() const { return width; }
    int getHeight() const { return height; }
};

#endif // GAME_OF_LIFE_H