// eslint-disable-next-line no-unused-vars
import React from 'react'
 import Navbar from './components/Navbar/Navbar'
// import { Slider } from '@mui/material';
import Slider from './components/Body/Slider'

function App() {
  return (
    <>
      <div>
        <Navbar />

        {/* <div className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold text-gray-800">
          Welcome to Bioscope
        </h1>
      </div> */}
      </div>
      <div className="w-full h-6 bg-slate-500  ">
        <p className='items-center text-center text-white'>We are Comming Soooon!!!</p>
      </div>
      <Slider />
    
    </>
  );
}

export default App
