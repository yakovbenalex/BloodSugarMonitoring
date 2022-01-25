package ru.opalevapps.EveryGlic.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ru.opalevapps.EveryGlic.R;

public class WelcomeScreenActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnNext;
    Button btnSkip;

    // --------------test viewPage
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private static final int WELCOME_SCREEN_PAGE_COUNT = 5;
    private static final String TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide actionBar
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setContentView(R.layout.activity_welcome_screen_slide);

        initViews();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // on page change listener for change next button text to "start"
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position + 1 >= WELCOME_SCREEN_PAGE_COUNT) {
                    btnNext.setText(getString(R.string.start));
                }
                if (position + 1 == WELCOME_SCREEN_PAGE_COUNT - 1) {
                    btnNext.setText(getString(R.string.next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                if (mViewPager.getCurrentItem() + 1 < WELCOME_SCREEN_PAGE_COUNT)
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                else this.finish();
                break;

            case R.id.btnSkip:
                this.finish();
                break;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        // KEYs
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_welcome_screen_slide_page, container, false);

            // find view by id
            TextView tvWelcomeScreenTitle = rootView.findViewById(R.id.tvWelcomeScreenTitle);
            TextView tvWelcomeScreenContent = rootView.findViewById(R.id.tvWelcomeScreenContent);
            ImageView ivWelcomeScreenImage = rootView.findViewById(R.id.ivWelcomeScreenImage);

            // get section number (page number)
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            // resId vars
            int idWelcomeScreenTitle = R.string.welcome_screen1_title;
            int idWelcomeScreenImage = R.drawable.ic_logo_font_1;
            int idWelcomeScreenContent = R.string.welcome_screen1_content;

            // select resId for content according to section number
            switch (sectionNumber) {
                case 1:
                    idWelcomeScreenTitle = R.string.welcome_screen1_title;
                    idWelcomeScreenImage = R.drawable.ic_logo_font_1;
                    idWelcomeScreenContent = R.string.welcome_screen1_content;
                    break;

                case 2:
                    idWelcomeScreenTitle = R.string.welcome_screen2_title;
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_add;
                    idWelcomeScreenContent = R.string.welcome_screen2_content;
                    break;

                case 3:
                    idWelcomeScreenTitle = R.string.welcome_screen3_title;
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_meas;
                    idWelcomeScreenContent = R.string.welcome_screen3_content;
                    break;

                case 4:
                    idWelcomeScreenTitle = R.string.welcome_screen4_title;
                    idWelcomeScreenImage = R.drawable.ic_welcome_screen_calc;
                    idWelcomeScreenContent = R.string.welcome_screen4_content;
                    break;

                case 5:
                    idWelcomeScreenTitle = R.string.welcome_screen5_title;
                    idWelcomeScreenImage = R.drawable.logo;
                    idWelcomeScreenContent = R.string.welcome_screen5_content;
                    break;

                default:
                    break;
            }

            // sets welcome screen content
            tvWelcomeScreenTitle.setText(getString(idWelcomeScreenTitle));
            ivWelcomeScreenImage.setImageDrawable(getResources().getDrawable(idWelcomeScreenImage));
            tvWelcomeScreenContent.setText(getString(idWelcomeScreenContent));

            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return WELCOME_SCREEN_PAGE_COUNT;
        }
    }

    // initialize views on screen and their listening
    public void initViews() {
        // find views by id
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        mViewPager = findViewById(R.id.welcome_screen_pager);

        // set listeners
        btnNext.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }
}
