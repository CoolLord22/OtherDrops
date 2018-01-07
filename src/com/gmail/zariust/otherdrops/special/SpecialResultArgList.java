// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.special;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class SpecialResultArgList extends AbstractSequentialList<String> {
    public class Iterator implements ListIterator<String> {
        private Node cur, active;
        private int  addCount;

        // An iterator where next() will throw but previous() will return the
        // value of the last node
        private Iterator() {
            cur = tail;
            active = null;
            addCount = modCount;
        }

        // An iterator where next() will return the value of the specified node
        private Iterator(Node next) {
            cur = next.prev;
            active = null;
            addCount = modCount;
        }

        @Override
        public void add(String val) {
            synchronized (SpecialResultArgList.this) {
                if (cur == null) {
                    // We're pointing at the start of the list, which may even
                    // be empty
                    cur = new Node(null, val, head);
                    if (head == null)
                        head = cur;
                } else {
                    // We're pointing at some specific node
                    Node n = new Node(cur, val, cur.next);
                    n.prev.next = n;
                    if (n.next != null)
                        n.next.prev = n;
                    cur = n;
                }
                // Check if the new element is now the last
                if (cur.next == null)
                    tail = cur;
                active = cur;
                size++;
                modCount++;
                addCount++;
            }
        }

        @Override
        public boolean hasNext() {
            if (cur == null) {
                if (head == null)
                    return false;
                return head.nextvalid();
            } else if (cur.next == null)
                return false;
            else
                return cur.next.nextvalid();
        }

        @Override
        public boolean hasPrevious() {
            if (cur == null)
                return false;
            return cur.prevvalid();
        }

        @Override
        public String next() throws NoSuchElementException {
            if (modCount > addCount)
                throw new ConcurrentModificationException();
            if (cur == null)
                cur = head;
            else if (!hasNext())
                throw new NoSuchElementException();
            else
                cur = cur.next;
            active = cur;
            if (active.deleted)
                return next();
            return cur.value;
        }

        @Override
        public int nextIndex() {
            if (cur.next == null)
                return size;
            return cur.next.index();
        }

        @Override
        public String previous() throws NoSuchElementException {
            if (modCount > addCount)
                throw new ConcurrentModificationException();
            if (cur == null)
                throw new NoSuchElementException();
            String val = cur.value;
            active = cur;
            cur = cur.prev;
            if (active.deleted)
                return previous();
            return val;
        }

        @Override
        public int previousIndex() {
            if (cur == null)
                return -1;
            return cur.index();
        }

        @Override
        public void remove() throws IllegalStateException {
            synchronized (SpecialResultArgList.this) {
                if (active == null || active.deleted)
                    throw new IllegalStateException();
                if (active.prev == null)
                    head = active.next;
                else
                    active.prev.next = active.next;
                if (active.next == null)
                    tail = active.prev;
                else
                    active.next.prev = active.prev;
                active.deleted = true;
                cur = cur.prev; // so that remove followed by add works
                size--;
            }
        }

        @Override
        public void set(String val) throws IllegalStateException {
            if (active == null || active.deleted)
                throw new IllegalStateException();
            active.value = val;
        }

        public String get() {
            if (active == null)
                throw new IllegalStateException();
            return active.value;
        }
    }

    private Node         head, tail;
    private volatile int size;

    public SpecialResultArgList() {
        head = tail = null;
        size = 0;
    }

    public SpecialResultArgList(Collection<String> other) {
        addAll(other);
    }

    public SpecialResultArgList(String... array) {
        Collections.addAll(this, array);
    }

    @Override
    public Iterator listIterator(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i > size)
            throw new IndexOutOfBoundsException(Integer.toString(i));
        if (i == size)
            return new Iterator();
        else if (i > size / 2) {
            Node at = tail;
            for (; ++i < size; at = at.prev)
                ;
            return new Iterator(at);
        } else {
            Node at = head;
            for (; i-- > 0; at = at.next)
                ;
            return new Iterator(at);
        }
    }

    @Override
    public Iterator listIterator() throws IndexOutOfBoundsException {
        return (Iterator) super.listIterator();
    }

    @Override
    public int size() {
        return size;
    }

    private class Node {
        Node    prev;
        String  value;
        Node    next;
        boolean deleted = false;

        public Node(Node before, String val, Node after) {
            prev = before;
            value = val;
            next = after;
        }

        public boolean nextvalid() {
            if (!deleted)
                return true;
            return next.nextvalid();
        }

        public boolean prevvalid() {
            if (!deleted)
                return true;
            return prev.prevvalid();
        }

        public int index() {
            if (prev == null)
                return 0;
            return prev.index() + 1;
        }
    }

    // This is my testing function to make sure the list behaves as I want.
    public static void main(String[] args) {
        // Testing concurrent removal
        SpecialResultArgList list = new SpecialResultArgList();
        list.add("PING");
        list.add("PONG");
        list.add("STRING");
        list.add("CLANG");
        list.add("BOOM");
        System.out.println(list.toString() + " " + list.size());
        SpecialResultArgList.Iterator iter1, iter2;
        iter1 = list.listIterator(2);
        iter2 = list.listIterator(2);
        System.out.println("iter1 and iter2 pointing at element 2");
        System.out.println("iter1: " + iter1.next() + ", iter2: "
                + iter2.next());
        iter1.remove();
        System.out.println("Removed through iter1");
        System.out.println("Advancing iter2 to " + iter2.next());
        iter2.remove();
        System.out.println("Removed through iter2");
        System.out.println("Advancing iter1 to " + iter1.next());
        System.out.println("Advancing iter2 to " + iter2.next());
        System.out.println(list.toString() + " " + list.size());
        // More testing concurrent removal
        System.out.println("---");
        list = new SpecialResultArgList("PING", "PONG", "CLANG", "PRANG",
                "BOOM", "BANG", "SPLOOSH");
        System.out.println(list.toString() + " " + list.size());
        for (String str : list) {
            if (str.startsWith("P"))
                list.remove(str);
        }
        System.out.println(list.toString() + " " + list.size());
        // // Testing concurrent addition (commented out because it throws an
        // exception, as it should)
        // System.out.println("---");
        // list = new
        // DropEventArgList("PING","PONG","CLANG","PRANG","BOOM","BANG","SPLOOSH");
        // System.out.println(list.toString() + " " + list.size());
        // int i = 1;
        // for(String str : list) {
        // list.add(i, str + "ED");
        // }
        // System.out.println(list.toString() + " " + list.size());
        // Testing typical application
        System.out.println("---");
        list = new SpecialResultArgList("RADIUS=3", "HEIGHT=5", "QUACK", "MOO");
        System.out.println(list.toString() + " " + list.size());
        for (String arg : list) {
            if (arg.startsWith("RADIUS"))
                list.remove(arg);
            else if (arg.startsWith("HEIGHT"))
                list.remove(arg);
        }
        System.out.println(list.toString() + " " + list.size());
        // Profiling
        System.out.println("---");
        Random rng = new Random();
        profile(rng, 10);
        profile(rng, 100);
        profile(rng, 1000);
        profile(rng, 5000);
    }

    private static void profile(Random rng, int n) {
        System.out.println("Add and remove " + n + " elements:");
        SpecialResultArgList list = new SpecialResultArgList();
        long start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            byte[] rand = new byte[5];
            char[] arg = new char[5];
            rng.nextBytes(rand);
            for (int j = 0; j < 5; j++)
                arg[j] = (char) rand[j];
            list.add(String.valueOf(arg));
        }
        long end = System.currentTimeMillis();
        long add = end - start;
        System.out.println("Time taken to add: " + add + "ms");
        assert list.size() == n;
        SpecialResultArgList.Iterator iter = list.listIterator();
        start = System.currentTimeMillis();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
        end = System.currentTimeMillis();
        long remove = end - start;
        System.out.println("Time taken to remove: " + remove + "ms");
        System.out.println("Total time: " + (add + remove) + "ms");
    }
}
