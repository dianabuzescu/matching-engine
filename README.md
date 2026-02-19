# High-Performance Trading Order Matching Engine

A simplified high-performance trading order matching engine inspired by real-world financial exchange systems.

Designed to demonstrate:

- Low-latency in-memory matching
- Deterministic per-symbol processing
- Asynchronous ingestion
- Concurrency-safe architecture
- Performance instrumentation & benchmarking

## Overview

This project implements a simplified order matching engine similar to those used in financial trading infrastructure.

It supports:

- Order submission via REST API
- Per-symbol order books
- Price-time priority matching
- Asynchronous order processing
- Real-time trade generation
- Performance metrics & throughput benchmarking

The system is fully in-memory and optimized for low-latency processing.

## Architecture

The system follows a modular architecture:

- **API Layer** – REST controllers (order ingestion, metrics, benchmarking)
- **Dispatcher Layer** – Per-symbol worker model with FIFO queues
- **Matching Engine** – PriorityQueue-based price-time matching logic
- **Trade Store** – In-memory trade persistence
- **Metrics Store** – Throughput & latency tracking


### High-Level Flow

```
Client
  |
  v
POST /orders
  |
  v
SymbolOrderDispatcher
  |
  v
Per-Symbol FIFO Queue
  |
  v
Single-Thread Worker
  |
  v
MatchingEngine (PriorityQueue Bids/Asks)
  |
  v
TradeStore + MetricsStore
```

## Design Decisions

### 1. Per-Symbol Single-Thread Workers

Each trading symbol is processed by a dedicated single-threaded worker.

Benefits:
- Deterministic order processing
- No coarse locking
- No race conditions inside the matching engine
- Natural scalability across symbols

This mimics real-world exchange partitioning strategies.

---

### 2. In-Memory Order Books

Order books are implemented using:

- PriorityQueue for bids (max-heap by price, then time)
- PriorityQueue for asks (min-heap by price, then time)

Matching follows strict price-time priority.

---

### 3. Asynchronous Ingestion (Fire-and-Forget)

Order submission returns immediately (HTTP 202 Accepted).
Matching occurs asynchronously in background workers.

This models real exchange ingestion pipelines.

---

### 4. Performance Instrumentation

The engine tracks:

- Total orders processed
- Total trades generated
- Matching compute time (nanosecond precision)
- End-to-end throughput (orders/sec, trades/sec)


## Benchmark Results (Local Machine)

Load test: 5,000 orders (single symbol)

- End-to-end processing time: ~13.6 ms
- Throughput: ~368,000 orders/sec
- Trades generated: ~3,750
- Matching compute latency: ~1–2 microseconds per order

Notes:
- Results depend on JVM warm-up and hardware.
- Measurements exclude external I/O.


## Running the Application

Start the application:

```bash
./gradlew bootRun
```

Submit a load test:

```bash
curl -X POST "http://localhost:8080/load?symbol=AAPL&n=5000&wait=true"
```

Check metrics:

```bash
curl http://localhost:8080/metrics
```

## Concurrency Testing

A JUnit concurrency test validates:

- No race conditions under parallel order submission
- No lost or duplicated trades
- Order book consistency after concurrent execution


## Future Improvements

- Persistent storage layer
- Multi-node distribution
- Lock-free ring-buffer ingestion (Disruptor-style)
- Latency percentiles (p50, p99)
- Multi-symbol benchmark aggregation
