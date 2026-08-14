//
// Created by marti on 9/8/2026.
//
#include "GameOfLife.h"

// Chunk constructor
Chunk::Chunk(int start_x, int end_x, int start_y, int end_y)
        : is_active(false),
          chunk_start_x(start_x), chunk_end_x(end_x),
          chunk_start_y(start_y), chunk_end_y(end_y),
          min_x(0), max_x(0), min_y(0), max_y(0),
          is_active_next(false),
          next_min_x(end_x), next_max_x(-1), next_min_y(end_y), next_max_y(-1) {}

// GameOfLife constructor
GameOfLife::GameOfLife(int w, int h, int c, int patternIndex)
        : width(w),
          height(h),
          grid_current(w * h, 0),
          grid_next(w * h, 0),
          chunk_size(c),
          num_chunks_x(w / c),
          num_chunks_y(h / c)
{
    if (w % c != 0 || h % c != 0) {
        throw std::invalid_argument("Anchura y altura deben ser divisibles por el tamaño del chunk.");
    }

    // Initialize the chunk grid
    for (int dy = 0; dy < num_chunks_y; ++dy) {
        for (int dx = 0; dx < num_chunks_x; ++dx) {
            int start_x = dx * chunk_size;
            int end_x = start_x + chunk_size;
            int start_y = dy * chunk_size;
            int end_y = start_y + chunk_size;
            chunks_current.emplace_back(start_x, end_x, start_y, end_y);
        }
    }

    // Precalculate neighbors for each chunk
    for (int cy = 0; cy < num_chunks_y; ++cy) {
        for (int cx = 0; cx < num_chunks_x; ++cx) {
            int idx = cy * num_chunks_x + cx;
            int n_idx = 0;
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dx = -1; dx <= 1; ++dx) {
                    int nx = (cx + dx + num_chunks_x) % num_chunks_x;
                    int ny = (cy + dy + num_chunks_y) % num_chunks_y;
                    chunks_current[idx].neighbors[n_idx++] = ny * num_chunks_x + nx;
                }
            }
        }
    }

    // Calculate the approximate center of the screen to center the patterns
    int cx = w / 2;
    int cy = h / 2;

    // Define the initial automaton
    switch (patternIndex) {
        case 0: // Option 0: A simple Glider
            if (w > 3 && h > 3) {
                setCell(cx, cy - 1);
                setCell(cx + 1, cy);
                setCell(cx - 1, cy + 1);
                setCell(cx, cy + 1);
                setCell(cx + 1, cy + 1);
            }
            break;

        case 1: // Option 1: A simple Pulsar
            if (w >= 15 && h >= 15) {
                // Centered at (cx, cy) - Size 15x15
                // Top Left Quadrant
                for (int x = cx - 4; x <= cx + 1; ++x) {
                    setCell(x, cy - 4);
                    setCell(x, cy - 3);
                }
                // Top Right Quadrant
                for (int y = cy - 4; y <= cy + 1; ++y) {
                    setCell(cx + 3, y);
                    setCell(cx + 4, y);
                }
                // Bottom Left Quadrant
                for (int x = cx - 1; x <= cx + 4; ++x) {
                    setCell(x, cy + 3);
                    setCell(x, cy + 4);
                }
                // Bottom Right Quadrant
                for (int y = cy - 1; y <= cy + 4; ++y) {
                    setCell(cx - 4, y);
                    setCell(cx - 3, y);
                }
            }
            break;


        case 2: // Option 2: Pentadecathlon
            if (w >= 5 && h >= 12) {
                // Top quadrant
                setCell(cx, cy - 4);
                setCell(cx - 1, cy - 3);
                setCell(cx + 1, cy - 3);
                // Body
                setCell(cx, cy - 2);
                setCell(cx, cy - 1);
                setCell(cx, cy);
                setCell(cx, cy + 1);
                // Bottom quadrant
                setCell(cx - 1, cy + 2);
                setCell(cx + 1, cy + 2);
                setCell(cx, cy + 3);
            }
            break;

        case 3: // Option 3: Lightweight Spaceship (LWSS)
            if (w > 6 && h > 6) {
                setCell(cx - 1, cy - 2); setCell(cx + 2, cy - 2);
                setCell(cx - 2, cy - 1);
                setCell(cx - 2, cy);     setCell(cx + 2, cy);
                setCell(cx - 2, cy + 1); setCell(cx - 1, cy + 1);
                setCell(cx, cy + 1); setCell(cx + 1, cy + 1);
            }
            break;

        case 4: // Option 4: Acorn
            if (w >= 5 && h >= 9) {
                setCell(cx + 1, cy - 2);
                setCell(cx, cy);
                setCell(cx - 1, cy - 3);
                setCell(cx - 1, cy - 2);
                setCell(cx - 1, cy + 1);
                setCell(cx - 1, cy + 2);
                setCell(cx - 1, cy + 3);
            }
            break;


        case 5: // Option 5: Random fill
            // Here we use rand() from <cstdlib>
            // to iterate over the matrix and initialize cells randomly.
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    if (rand() % 100 < 20) { // 20% of probability to be alive
                        setCell(x, y);
                    }
                }
            }
            break;

        default:
            // By default, draw a simple static block (case 6)
            setCell(cx, cy);
            setCell(cx + 1, cy);
            setCell(cx, cy + 1);
            setCell(cx + 1, cy + 1);
            break;
    }
}


int GameOfLife::countAliveBorderNeighbors(int x, int y) {
    uint8_t n_neigh = 0;
    for (int dy = -1; dy < 2; dy += 2) {
        for (int dx = -1; dx < 2; dx++) {
            n_neigh += grid_current[getIndex(x + dx, y + dy)];
        }
    }
    n_neigh += grid_current[getIndex(x - 1, y)];
    n_neigh += grid_current[getIndex(x + 1, y)];
    return n_neigh;
}

int GameOfLife::countAliveInteriorNeighbors(int x, int y) {
    uint8_t n_neigh = 0;
    int index = getIndex(x, y);
    for (int dy = -1; dy < 2; dy += 2) {
        int pos = index + dy * width;
        for (int dx = -1; dx < 2; dx++) {
            n_neigh += grid_current[pos + dx];
        }
    }
    n_neigh += grid_current[index - 1];
    n_neigh += grid_current[index + 1];
    return n_neigh;
}

uint8_t GameOfLife::rulesConway(int x, int y, int neigh) {
    uint8_t alive = grid_current[getIndex(x, y)];
    if (alive == 0 && neigh == 3) return 1;
    if (alive == 1 && (neigh < 2 || neigh > 3)) return 0;
    return alive;
}

void GameOfLife::setCell(int x, int y) {
    int idx = getIndex(x, y);
    grid_current[idx] ^= 1;

    if (grid_current[idx]) {
        // Instead of activating only the central cell's chunk, precompute a 3x3 "halo"
        // to guarantee Generation 1 properly evaluates adjacent boundaries.
        int points_x[] = {x - 1, x, x + 1};
        int points_y[] = {y - 1, y, y + 1};

        for (int px : points_x) {
            for (int py : points_y) {
                // Safe coordinate wrap-around respecting toroidal topology
                int real_x = ((px % width) + width) % width;
                int real_y = ((py % height) + height) % height;

                // Locate target chunk for this halo coordinate
                int chunk_x = real_x / chunk_size;
                int chunk_y = real_y / chunk_size;
                int chunk_index = chunk_y * num_chunks_x + chunk_x;

                Chunk& chunk = chunks_current[chunk_index];

                // Wake up dormant chunk and set initial bounding box
                if (!chunk.is_active) {
                    chunk.is_active = true;
                    chunk.min_x = real_x; chunk.max_x = real_x;
                    chunk.min_y = real_y; chunk.max_y = real_y;
                } else {
                    // Expand active chunk boundaries if already awake
                    chunk.min_x = std::min(chunk.min_x, real_x);
                    chunk.max_x = std::max(chunk.max_x, real_x);
                    chunk.min_y = std::min(chunk.min_y, real_y);
                    chunk.max_y = std::max(chunk.max_y, real_y);
                }
            }
        }
    }
}

void GameOfLife::update() {
    std::fill(grid_next.begin(), grid_next.end(), 0);

    for (size_t i = 0; i < chunks_current.size(); ++i) {
        Chunk& chunk = chunks_current[i];
        if (!chunk.is_active) continue;

        int start_x = std::max(chunk.chunk_start_x, chunk.min_x - 1);
        int end_x = std::min(chunk.chunk_end_x, chunk.max_x + 2);
        int start_y = std::max(chunk.chunk_start_y, chunk.min_y - 1);
        int end_y = std::min(chunk.chunk_end_y, chunk.max_y + 2);

        int neigh = 0;
        int new_min_x = chunk.chunk_end_x, new_max_x = -1;
        int new_min_y = chunk.chunk_end_y, new_max_y = -1;

        bool is_global_border = (start_x == 0 || end_x == width || start_y == 0 || end_y == height);

        if (is_global_border) {
            if (start_x == 0) {
                int dx = start_x;
                for (int dy = start_y; dy < end_y; ++dy) {
                    neigh = countAliveBorderNeighbors(dx, dy);
                    grid_next[getIndex(dx, dy)] = rulesConway(dx, dy, neigh);
                    if (grid_next[getIndex(dx, dy)] == 1) {
                        new_min_x = std::min(new_min_x, dx); new_max_x = std::max(new_max_x, dx);
                        new_min_y = std::min(new_min_y, dy); new_max_y = std::max(new_max_y, dy);
                    }
                }
                start_x++;
            }
            if (end_x == width) {
                int dx = end_x - 1;
                for (int dy = start_y; dy < end_y; ++dy) {
                    neigh = countAliveBorderNeighbors(dx, dy);
                    grid_next[getIndex(dx, dy)] = rulesConway(dx, dy, neigh);
                    if (grid_next[getIndex(dx, dy)] == 1) {
                        new_min_x = std::min(new_min_x, dx); new_max_x = std::max(new_max_x, dx);
                        new_min_y = std::min(new_min_y, dy); new_max_y = std::max(new_max_y, dy);
                    }
                }
                end_x--;
            }
            if (start_y == 0) {
                int dy = start_y;
                for (int dx = start_x; dx < end_x; ++dx) {
                    neigh = countAliveBorderNeighbors(dx, dy);
                    grid_next[getIndex(dx, dy)] = rulesConway(dx, dy, neigh);
                    if (grid_next[getIndex(dx, dy)] == 1) {
                        new_min_x = std::min(new_min_x, dx); new_max_x = std::max(new_max_x, dx);
                        new_min_y = std::min(new_min_y, dy); new_max_y = std::max(new_max_y, dy);
                    }
                }
                start_y++;
            }
            if (end_y == height) {
                int dy = end_y - 1;
                for (int dx = start_x; dx < end_x; ++dx) {
                    neigh = countAliveBorderNeighbors(dx, dy);
                    grid_next[getIndex(dx, dy)] = rulesConway(dx, dy, neigh);
                    if (grid_next[getIndex(dx, dy)] == 1) {
                        new_min_x = std::min(new_min_x, dx); new_max_x = std::max(new_max_x, dx);
                        new_min_y = std::min(new_min_y, dy); new_max_y = std::max(new_max_y, dy);
                    }
                }
                end_y--;
            }
        }

        for (int dy = start_y; dy < end_y; ++dy) {
            for (int dx = start_x; dx < end_x; ++dx) {
                neigh = countAliveInteriorNeighbors(dx, dy);
                grid_next[getIndex(dx, dy)] = rulesConway(dx, dy, neigh);
                if (grid_next[getIndex(dx, dy)] == 1) {
                    new_min_x = std::min(new_min_x, dx); new_max_x = std::max(new_max_x, dx);
                    new_min_y = std::min(new_min_y, dy); new_max_y = std::max(new_max_y, dy);
                }
            }
        }

        if (new_max_x != -1) {
            int points_x[] = {new_min_x - 1, new_min_x, new_max_x, new_max_x + 1};
            int points_y[] = {new_min_y - 1, new_min_y, new_max_y, new_max_y + 1};

            for (int px : points_x) {
                for (int py : points_y) {
                    int nx = 1;
                    if (px < chunk.chunk_start_x) nx = 0;
                    else if (px >= chunk.chunk_end_x) nx = 2;

                    int ny = 1;
                    if (py < chunk.chunk_start_y) ny = 0;
                    else if (py >= chunk.chunk_end_y) ny = 2;

                    int n_idx = ny * 3 + nx;
                    int c_idx = chunk.neighbors[n_idx];

                    int real_x = ((px % width) + width) % width;
                    int real_y = ((py % height) + height) % height;

                    chunks_current[c_idx].is_active_next = true;
                    chunks_current[c_idx].next_min_x = std::min(chunks_current[c_idx].next_min_x, real_x);
                    chunks_current[c_idx].next_max_x = std::max(chunks_current[c_idx].next_max_x, real_x);
                    chunks_current[c_idx].next_min_y = std::min(chunks_current[c_idx].next_min_y, real_y);
                    chunks_current[c_idx].next_max_y = std::max(chunks_current[c_idx].next_max_y, real_y);
                }
            }
        }
    }

    for (size_t i = 0; i < chunks_current.size(); ++i) {
        chunks_current[i].is_active = chunks_current[i].is_active_next;
        chunks_current[i].min_x = chunks_current[i].next_min_x;
        chunks_current[i].max_x = chunks_current[i].next_max_x;
        chunks_current[i].min_y = chunks_current[i].next_min_y;
        chunks_current[i].max_y = chunks_current[i].next_max_y;

        chunks_current[i].is_active_next = false;
        chunks_current[i].next_min_x = chunks_current[i].chunk_end_x;
        chunks_current[i].next_max_x = -1;
        chunks_current[i].next_min_y = chunks_current[i].chunk_end_y;
        chunks_current[i].next_max_y = -1;
    }

    std::swap(grid_current, grid_next);
}