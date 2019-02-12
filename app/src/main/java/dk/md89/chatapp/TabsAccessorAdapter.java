package dk.md89.chatapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter

{

    public TabsAccessorAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)                                                                  //Here we put the positions (i) of the fragments in the TabLayout
    {
        switch (i)
        {
            case 0:
                ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
                return chatRoomFragment;

            case 1:
                InboxFragment indboxFragment = new InboxFragment();
                return indboxFragment;

            case 2:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;

                default:
                return null;
        }


    }

    @Override
    public int getCount()

    {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position)                                                  //Here we set the title for each fragment position.

    {
        switch (position)
        {
            case 0:
                return "Chat Rooms";

            case 1:
                return "Inbox";

            case 2:
                return "Contacts";


            default:
                return null;
        }
    }
}
