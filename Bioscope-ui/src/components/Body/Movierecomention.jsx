import React from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import Movie from "./Movicard";

const movies = [
  {
    title: "The Lord of the Rings: The War of the Rohirrim",
    poster: "/placeholder.svg?height=360&width=240",
    likes: 4.6,
    genres: ["Action", "Animation", "Fantasy"],
  },
  {
    title: "Heretic",
    poster: "/placeholder.svg?height=360&width=240",
    likes: 4,
    genres: ["Horror", "Thriller"],
  },
  {
    title: "Solo Leveling: ReAwakening",
    poster: "/placeholder.svg?height=360&width=240",
    rating: 9.5,
    votes: 1.3,
    genres: ["Action", "Adventure", "Anime", "Fantasy"],
  },
  {
    title: "Mahayogi: Highway 1 to Oneness",
    poster: "/placeholder.svg?height=360&width=240",
    likes: 18,
    genres: ["Drama"],
  },
  {
    title: "All We Imagine as Light (Eng Sub)",
    poster: "/placeholder.svg?height=360&width=240",
    rating: 8.2,
    votes: 1.7,
    genres: ["Drama"],
  },
  {
    title: "All We Imagine as Light (Eng Sub)",
    poster: "/placeholder.svg?height=360&width=240",
    rating: 8.2,
    votes: 1.7,
    genres: ["Drama"],
  },
  {
    title: "All We Imagine as Light (Eng Sub)",
    poster: "/placeholder.svg?height=360&width=240",
    rating: 8.2,
    votes: 1.7,
    genres: ["Drama"],
  },
];

function MovieRecommendations() {
  return React.createElement(
    "div",
    { className: "p-6" },
    React.createElement(
      "div",
      { className: "mb-4 flex items-center justify-between" },
      React.createElement(
        "h2",
        { className: "text-2xl font-bold" },
        "Recommended Movies"
      ),
      React.createElement(
        "button",
        { className: "text-red-500 hover:underline" },
        "See All"
      )
    ),
    React.createElement(
      "div",
      { className: "relative" },
      React.createElement(
        "div",
        { className: "flex gap-4 overflow-x-auto pb-4 scrollbar-hide" },
        movies.map((movie, index) =>
          React.createElement(Movie, { key: index, ...movie })
        )
      ),
      React.createElement(
        "button",
        {
          className:
            "absolute -left-4 top-1/2 -translate-y-1/2 bg-black/50 text-white hover:bg-black/70 p-2 rounded-full",
        },
        React.createElement(ChevronLeft, { className: "h-8 w-8" })
      ),
      React.createElement(
        "button",
        {
          className:
            "absolute -right-4 top-1/2 -translate-y-1/2 bg-black/50 text-white hover:bg-black/70 p-2 rounded-full",
        },
        React.createElement(ChevronRight, { className: "h-8 w-8" })
      )
    )
  );
}

export default MovieRecommendations;
