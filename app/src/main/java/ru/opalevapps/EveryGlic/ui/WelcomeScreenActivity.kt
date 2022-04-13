package ru.opalevapps.EveryGlic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import ru.opalevapps.EveryGlic.R

class WelcomeScreenActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button

    // --------------test viewPage
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // hide actionBar
        if (supportActionBar != null) supportActionBar!!.hide()
        setContentView(R.layout.activity_welcome_screen_slide)
        initViews()

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager?.adapter = mSectionsPagerAdapter

        // on page change listener for change next button text to "start"
        mViewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position + 1 >= WELCOME_SCREEN_PAGE_COUNT) {
                    btnNext.text = getString(R.string.start)
                }
                if (position + 1 == WELCOME_SCREEN_PAGE_COUNT - 1) {
                    btnNext.text = getString(R.string.next)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnNext -> if (mViewPager!!.currentItem + 1 < WELCOME_SCREEN_PAGE_COUNT) mViewPager!!.currentItem =
                mViewPager!!.currentItem + 1 else finish()
            R.id.btnSkip -> finish()
        }
    }

    class PlaceholderFragment : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val rootView =
                inflater.inflate(R.layout.fragment_welcome_screen_slide_page, container, false)

            // find view by id
            val tvWelcomeScreenTitle = rootView.findViewById<TextView>(R.id.tvWelcomeScreenTitle)
            val tvWelcomeScreenContent =
                rootView.findViewById<TextView>(R.id.tvWelcomeScreenContent)
            val ivWelcomeScreenImage = rootView.findViewById<ImageView>(R.id.ivWelcomeScreenImage)

            // get section number (page number)
            val sectionNumber = requireArguments().getInt(ARG_SECTION_NUMBER)

            // resId vars
            var idWelcomeScreenTitle = R.string.welcome_screen1_title
            var idWelcomeScreenImage = R.drawable.ic_logo_font_1
            var idWelcomeScreenContent = R.string.welcome_screen1_content
            when (sectionNumber) {
                1 -> {
                    idWelcomeScreenTitle = R.string.welcome_screen1_title
                    idWelcomeScreenImage = R.drawable.ic_logo_font_1
                    idWelcomeScreenContent = R.string.welcome_screen1_content
                }
                2 -> {
                    idWelcomeScreenTitle = R.string.welcome_screen2_title
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_add
                    idWelcomeScreenContent = R.string.welcome_screen2_content
                }
                3 -> {
                    idWelcomeScreenTitle = R.string.welcome_screen3_title
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_meas
                    idWelcomeScreenContent = R.string.welcome_screen3_content
                }
                4 -> {
                    idWelcomeScreenTitle = R.string.welcome_screen4_title
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_calc
                    idWelcomeScreenContent = R.string.welcome_screen4_content
                }
                5 -> {
                    idWelcomeScreenTitle = R.string.welcome_screen5_title
                    idWelcomeScreenImage = R.drawable.logo
                    idWelcomeScreenContent = R.string.welcome_screen5_content
                }
                else -> {}
            }

            // sets welcome screen content
            tvWelcomeScreenTitle.text = getString(idWelcomeScreenTitle)
            ivWelcomeScreenImage.setImageDrawable(resources.getDrawable(idWelcomeScreenImage))
            tvWelcomeScreenContent.text = getString(idWelcomeScreenContent)
            return rootView
        }

        companion object {
            // KEYs
            private const val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    inner class SectionsPagerAdapter internal constructor(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show total pages.
            return WELCOME_SCREEN_PAGE_COUNT
        }
    }

    // initialize views on screen and their listening
    private fun initViews() {
        // find views by id
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)
        mViewPager = findViewById(R.id.welcome_screen_pager)

        // set listeners
        btnNext.setOnClickListener(this)
        btnSkip.setOnClickListener(this)
    }

    companion object {
        private const val WELCOME_SCREEN_PAGE_COUNT = 5
        private const val TAG = "myLog"
    }
}