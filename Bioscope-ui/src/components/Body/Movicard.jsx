import React from "react";
import { FanIcon } from "lucide-react";

import PropTypes from "prop-types";

Movie.propTypes = {
  title: PropTypes.string,
  likes: PropTypes.number,
  poster: PropTypes.string,
  rating: PropTypes.number,
  votes: PropTypes.number,
  genres: PropTypes.array,
};

function Movie({ title, poster, likes, rating, votes, genres }) {
  return React.createElement(
    "div",
    {
      className:
        "w-[240px] overflow-hidden bg-black text-white rounded-lg shadow-md",
    },
    React.createElement(
      "div",
      { className: "relative h-[360px]" },
      React.createElement("img", {
        src: poster,
        alt: title,
        className: "h-full w-full object-cover",
      }),
      React.createElement(
        "div",
        {
          className:
            "absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black to-transparent p-4",
        },
        likes &&
          React.createElement(
            "div",
            { className: "flex items-center gap-1" },
            React.createElement(FanIcon, {
              className: "h-4 w-4 text-green-500",
            }),
            React.createElement(
              "span",
              { className: "text-sm" },
              `${likes} Likes`
            )
          ),
        rating &&
          React.createElement(
            "div",
            { className: "flex items-center gap-1" },
            React.createElement(
              "span",
              { className: "text-sm text-red-500" },
              `${rating}/10`
            ),
            React.createElement(
              "span",
              { className: "text-sm text-gray-400" },
              `${votes}K Votes`
            )
          )
      )
    ),
    React.createElement(
      "div",
      { className: "p-3" },
      React.createElement(
        "h3",
        { className: "font-semibold leading-tight" },
        title
      ),
      React.createElement(
        "p",
        { className: "mt-1 text-sm text-gray-400" },
        genres.join(" / ")
      )
    )
  );
}

export default Movie;
